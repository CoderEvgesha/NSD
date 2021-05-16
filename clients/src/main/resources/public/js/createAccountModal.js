"use strict";

angular.module('demoAppModule').controller('CreateAccountModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL) {
    const createAccountModal = this;

    createAccountModal.form = {};
    createAccountModal.formError = false;

    /** Validate and create an account. */
    createAccountModal.create = () => {
        if (invalidFormInput()) {
            createAccountModal.formError = true;
        } else {
            createAccountModal.formError = false;

            const account = createAccountModal.form.account;

            $uibModalInstance.close();

            // We define the account creation endpoint.
            const createAccountEndpoint =
                apiBaseURL +
                `create-account/${account}`;

            // We hit the endpoint to create the account and handle success/failure responses.
            $http.post(createAccountEndpoint).then(
                (result) => createAccountModal.displayMessage(result),
                (result) => createAccountModal.displayMessage(result)
            );
        }
    };

    /** Displays the success/failure response from attempting to create an account. */
    createAccountModal.displayMessage = (message) => {
        const createAccountMsgModal = $uibModal.open({
            templateUrl: 'createAccountMsgModal.html',
            controller: 'createAccountMsgModalCtrl',
            controllerAs: 'createAccountMsgModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createAccountMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the account creation modal. */
    createAccountModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the Account.
    function invalidFormInput() {
        return (createAccountModal.form.account === undefined);
    }
});

// Controller for the success/fail modal.
angular.module('demoAppModule').controller('createAccountMsgModalCtrl', function($uibModalInstance, message) {
    const createAccountMsgModal = this;
    createAccountMsgModal.message = message.data;
});