"use strict";

// Define your backend here.
angular.module('demoAppModule', ['ui.bootstrap']).controller('DemoAppCtrl', function($http, $location, $uibModal) {
    const demoApp = this;

    const apiBaseURL = "/api/iou/";

    // Retrieves the identity of this and other nodes.
    let accounts = [];
    $http.get(apiBaseURL + "me").then((response) => demoApp.thisNode = response.data.me.match(/O=([^,]+)/)[1]);
    $http.get(apiBaseURL + "accounts").then((response) => accounts = response.data);

    /** Displays the Account creation modal. */
    demoApp.openCreateAccountModal = () => {
        const createAccountModal = $uibModal.open({
            templateUrl: 'createAccountModal.html',
            controller: 'CreateAccountModalCtrl',
            controllerAs: 'createAccountModal',
            resolve: {
                apiBaseURL: () => apiBaseURL
            }
        });

        // Ignores the modal result events.
        createAccountModal.result.then(() => {}, () => {});
    };
    
    /** Displays the IOU creation modal. */
    demoApp.openCreateIOUModal = () => {
        const createIOUModal = $uibModal.open({
            templateUrl: 'createIOUModal.html',
            controller: 'CreateIOUModalCtrl',
            controllerAs: 'createIOUModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                accounts: () => accounts
            }
        });

        // Ignores the modal result events.
        createIOUModal.result.then(() => {}, () => {});
    };

    /** Displays the cash issuance modal. */
    demoApp.openIssueCashModal = () => {
        const issueCashModal = $uibModal.open({
            templateUrl: 'issueCashModal.html',
            controller: 'IssueCashModalCtrl',
            controllerAs: 'issueCashModal',
            resolve: {
                apiBaseURL: () => apiBaseURL
            }
        });

        issueCashModal.result.then(() => {}, () => {});
    };

    /** Displays the IOU transfer modal. */
    demoApp.openTransferModal = (id) => {
        const transferModal = $uibModal.open({
            templateUrl: 'transferModal.html',
            controller: 'TransferModalCtrl',
            controllerAs: 'transferModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                accounts: () => accounts,
                id: () => id
            }
        });

        transferModal.result.then(() => {}, () => {});
    };

    /** Displays the IOU settlement modal. */
    demoApp.openSettleModal = (id) => {
        const settleModal = $uibModal.open({
            templateUrl: 'settleModal.html',
            controller: 'SettleModalCtrl',
            controllerAs: 'settleModal',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                id: () => id
            }
        });

        settleModal.result.then(() => {}, () => {});
    };

    /** Refreshes the front-end. */
    demoApp.refresh = () => {
        // Update the list of IOUs.
        $http.get(apiBaseURL + "ious").then((response) => demoApp.ious =
            Object.keys(response.data).map((key) => response.data[key].state.data));

        // Update the cash balances.
        $http.get(apiBaseURL + "cash-balances").then((response) => demoApp.cashBalances =
            response.data);
    }

    demoApp.refresh();
});

// Causes the webapp to ignore unhandled modal dismissals.
angular.module('demoAppModule').config(['$qProvider', function($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);