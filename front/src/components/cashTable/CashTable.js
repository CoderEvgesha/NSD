import React, {Component} from 'react';
import "./CashTable.css";

export class CashTable extends Component {

    render() {
        return (
            <div className="cash-table-container">
                <div className="cash-table-name">Остатки денежный средств</div>
                <div key={1} className="cash-row">
                    <div className="cash-table-sum">
                        500,000.00 руб.
                    </div>
                </div>
            </div>
        )
    }
}
