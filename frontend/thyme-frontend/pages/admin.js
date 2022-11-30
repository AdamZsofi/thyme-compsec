import React from 'react';
import ThymeHeader from '../components/header.js';

import { useState } from "react"
import Layout from "../components/layout"
import styles from "../styles/signin.module.css"
import validator from 'validator'
import { render } from 'react-dom';

function UserSelector(props) {
    return (
        <select name="usernames" id="usernames" value={props.value} onChange={props.handleChange}>
            {
                props.users.map((user) => {
                    return (
                        <option value={user.id}>{user.name}</option>
                    );
                })
            }
        </select>
    );
}

class ChangePassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: "",
            username: "",
            password: "",
            errorMessage: "Password field is empty",
        }
    }

    handlePwdChange(e) {
        if (validator.isStrongPassword(e.target.value, {
            minLength: 10, minLowercase: 1,
            minUppercase: 1, minNumbers: 1, minSymbols: 1
        })) {
            const copy = { ...state }
            copy[e.target.name] = e.target.value
            copy['errorMessage'] = 'Password OK'
            setState(copy)

        } else {
            const copy = { ...state }
            copy[e.target.name] = e.target.value
            copy['errorMessage'] = 'Password is not strong enough!'
            setState(copy)
        }
    }

    handleChange(e) {

    }

    handleSubmit(e) {
        alert("TODO: handle pwd change submit");
    }

    render() {
        return (
            <Layout>
                <div className={styles.container}>
                    <h1 className={styles.title}>Change Password of User</h1>
                    <div className={styles.form}>
                        <UserSelector 
                            value={this.state.id}
                            users={[]}
                            handleChange={this.handleChange}
                        />
                        <input className={styles.input} type="password" name="password" placeholder="new password" value={this.state.password} onChange={this.handlePwdChange} required />
                        <button className={styles.btn} onClick={this.handleSubmit}>Change Password</button>
                    </div>
                </div>
                {this.state.errorMessage === '' ? null :
                    <span style={{
                    fontWeight: 'bold',
                    color: 'red',
                    }}>{this.state.errorMessage}</span>}

                <div id="message">
                    <h3>Password must contain the following:</h3>
                    <p id="letter" className="invalid">A <b>lowercase</b> letter</p>
                    <p id="capital" className="invalid">A <b>capital (uppercase)</b> letter</p>
                    <p id="number" className="invalid">A <b>number</b></p>
                    <p id="number" className="invalid">A <b>symbol</b></p>
                    <p id="length" className="invalid">Minimum <b>10 characters</b></p>
                </div>
            </Layout>
        );
    };
}

class AdminPage extends React.Component {
    render() {
        return (
            <div>
                <ThymeHeader />
                <h1>Hi admin</h1>
                <div className="caff-page-row">
                    <div className="column">
                        <ChangePassword />
                    </div>
                    <div className="column">
                        <p>TODO: delete user form</p>
                    </div>
                </div>
            </div>
        );
    }
}
export default AdminPage