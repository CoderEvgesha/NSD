import React from 'react';
import { Switch, Route } from 'react-router-dom';
import { Login } from "./components/login/Login";
import { Main } from "./components/main/Main";
import './App.css';

function App() {
    return (
        <Switch>
            <Route exact path="/">
                <Login />
            </Route>
            <Route path="/main">
                <Main />
            </Route>
        </Switch>
    );
}

export default App;
