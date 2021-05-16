import React from 'react';
import './Header.css';
import logo from '../../nsd.svg'

export function Header() {
    return (
        <header className="header">
            <div className="header_container">
                <div className="header_logo_container">
                    <img src={logo} onClick={() => window.location.replace('/')} alt="Логотип НРД"/>
                </div>
            </div>
        </header>
    );
}
