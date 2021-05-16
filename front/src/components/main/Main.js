import React, {Component} from 'react';
//import AuthService from "./service/AuthService";
import {Header} from "../header/Header";
import {CashTable} from "../cashTable/CashTable";
import {CfaTable} from "../cfaTable/CfaTable";
import "./Main.css";

export class Main extends Component {

    render() {
        return (
            <>
                <Header/>
                <div className="main_container">
                    <div className="account_actions">
                        <div className="actor">Эмитент</div>
                        <div className="actions_btn ">Создать долговую расписку</div>
                        <div className="actions_btn ">Выпустить наличные</div>
                    </div>
                    <CashTable />
                    <CfaTable />
                </div>
            </>
        );
    }
}
