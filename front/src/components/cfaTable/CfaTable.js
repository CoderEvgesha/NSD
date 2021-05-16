import React, {Component} from 'react';
import "./CfaTable.css";

export class CfaTable extends Component {
    render() {
        return (
            <div className="cfa-table-container">
                <div className="cfa-table-name">Зарегистрированные долговые расписки</div>
                <div className="cfa-table-header">
                    <div className="cfa-table-header-text">
                        <span>Отправитель</span>
                    </div>
                    <div className="cfa-table-header-text">
                        <span>Получатель</span>
                    </div>
                    <div className="cfa-table-header-text">
                        <span>Количество</span>
                    </div>
                    <div className="cfa-table-header-text">
                        <span>Оплачено</span>
                    </div>
                    <div className="cfa-table-header-text">
                        <span>Действия</span>
                    </div>
                </div>
                <div key={1} className="cfa-row">
                    <div className="cfa-table-proposal-text">
                        Эмитент
                    </div>
                    <div className="cfa-table-proposal-text">
                        Алиса
                    </div>
                    <div className="cfa-table-proposal-text">
                        1000.00 руб.
                    </div>
                    <div className="cfa-table-proposal-text">
                        0.00 руб.
                    </div>
                    <div className="cfa-table-proposal-actions">
                       <div className="cfa-btn">Перевод</div>
                       <div className="cfa-btn">Решить</div>
                    </div>
                </div>
            </div>
        )
    }
}
