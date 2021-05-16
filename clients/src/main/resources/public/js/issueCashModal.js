"use strict";

// Similar to the IOU creation modal - see createIOUModal.js for comments.
angular.module('demoAppModule').controller('IssueCashModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, accounts) {
    const issueCashModal = this;

    issueCashModal.accounts = accounts;
    issueCashModal.form = {};
    issueCashModal.formError = false;

    issueCashModal.issue = () => {
        if (invalidFormInput()) {
            issueCashModal.formError = true;
        } else {
            issueCashModal.formError = false;

            const account = issueCashModal.form.account;
            const amount = issueCashModal.form.amount;

            $uibModalInstance.close();

            const issueCashEndpoint =
                apiBaseURL +
                `issue-cash?account=${account}&amount=${amount}`;

            $http.get(issueCashEndpoint).then(
                (result) => {console.log(result.toString()); issueCashModal.displayMessage(result); },
                (result) => {console.log(result.toString()); issueCashModal.displayMessage(result); }
            );
        }
    };

    issueCashModal.displayMessage = (message) => {
        const issueCashMsgModal = $uibModal.open({
            templateUrl: 'issueCashMsgModal.html',
            controller: 'issueCashMsgModalCtrl',
            controllerAs: 'issueCashMsgModal',
            resolve: {
                message: () => message
            }
        });

        issueCashMsgModal.result.then(() => {}, () => {});
    };

    issueCashModal.cancel = () => $uibModalInstance.dismiss();

    function invalidFormInput() {
        return isNaN(issueCashModal.form.amount);
    }
});

angular.module('demoAppModule').controller('issueCashMsgModalCtrl', function($uibModalInstance, message) {
    const issueCashMsgModal = this;
    issueCashMsgModal.message = message.data;
});