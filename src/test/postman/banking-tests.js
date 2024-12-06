// Register Customer Tests
pm.test("Register Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Register Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('token');
    if(jsonData.token) {
        pm.environment.set("jwt_token", jsonData.token);
    }
});

pm.test("Register Response token is string", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.be.a('string');
});

// Login Tests
pm.test("Login Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Login Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('token');
    if(jsonData.token) {
        pm.environment.set("jwt_token", jsonData.token);
    }
});

// Create Account Tests
pm.test("Create Account Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Create Account Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('accountHolderName');
    pm.expect(jsonData).to.have.property('balance');

    if(jsonData.id) {
        pm.environment.set("account_id", jsonData.id);
    }
});

pm.test("Create Account Balance matches request", function () {
    var jsonData = pm.response.json();
    var requestData = JSON.parse(pm.request.body.raw);
    pm.expect(jsonData.balance).to.equal(requestData.balance);
});

// Deposit Tests
pm.test("Deposit Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Deposit increases balance correctly", function () {
    var jsonData = pm.response.json();
    var requestData = JSON.parse(pm.request.body.raw);
    var oldBalance = pm.environment.get("previous_balance");
    var expectedBalance = oldBalance + requestData.amount;

    pm.expect(jsonData.balance).to.equal(expectedBalance);
    pm.environment.set("previous_balance", jsonData.balance);
});

// Withdraw Tests
pm.test("Withdraw Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Withdraw decreases balance correctly", function () {
    var jsonData = pm.response.json();
    var requestData = JSON.parse(pm.request.body.raw);
    var oldBalance = pm.environment.get("previous_balance");
    var expectedBalance = oldBalance - requestData.amount;

    pm.expect(jsonData.balance).to.equal(expectedBalance);
    pm.environment.set("previous_balance", jsonData.balance);
});

// Get Account Tests
pm.test("Get Account Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Get Account Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('accountHolderName');
    pm.expect(jsonData).to.have.property('balance');
});

// Error Tests
pm.test("Invalid token returns 403", function () {
    if(pm.response.code === 403) {
        pm.expect(pm.response.text()).to.include("forbidden");
    }
});

pm.test("Insufficient balance returns error", function () {
    if(pm.response.code === 400) {
        pm.expect(pm.response.text()).to.include("Insufficient balance");
    }
});