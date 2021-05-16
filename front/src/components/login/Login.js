import React, {Component} from 'react';
//import AuthService from "./service/AuthService";
import logo from '../../nsd.svg';
import "./Login.css";

export class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
            wrongCredentials: false
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        let value = target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    handleLogin = (e) => {
        /*e.preventDefault();
        const credentials = {username: this.state.username, password: this.state.password};
        AuthService.login(credentials).then(res => {
            if (res.status === 200) {
                window.location.replace('http://45.12.236.29:8080/');
            } else {
                this.setState({wrongCredentials: true});
            }
        })
            .catch(err => this.setState({wrongCredentials: true}));*/
        e.preventDefault();
        window.location.replace('/main');
    };

    render() {
        return (
            <div className="login_page_container">
                <div className="login_page_content">
                    <div className="login_page_logo_container">
                        <div className="login_page_logo_content">
                            <img src={logo} alt="Логотип Сбербанк"/>
                        </div>
                    </div>
                    <div className="login_page_input_container">
                        <div className={this.state.wrongCredentials ?
                            "login_page_input_content wrong"
                            : "login_page_input_content"}>
                            <form onSubmit={this.handleLogin} autoComplete="off">
                                <input className="login_input"
                                       name="username"
                                       placeholder="Логин"
                                       type="text"
                                       value={this.state.username}
                                       onChange={this.handleInputChange}
                                       required/>
                                <input className="password_input"
                                       name="password"
                                       placeholder={"Пароль"}
                                       type="password"
                                       value={this.state.password}
                                       onChange={this.handleInputChange}
                                       required/>
                                <div className="sign_in_btn_container">
                                    <button
                                        className="sign_in_btn"
                                        type="submit"
                                    >Войти
                                    </button>
                                </div>
                            </form>
                            {this.state.wrongCredentials ?
                                <div className="wrong_credentials_label">Неверный логин/пароль</div>
                                : null}
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
