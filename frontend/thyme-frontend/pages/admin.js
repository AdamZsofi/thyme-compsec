import React from 'react';
import ThymeHeader from '../components/header.js';

import Layout from "../components/layout"
import styles from "../styles/signin.module.css"
import validator from 'validator'
import { getUsers, postUserModification, postUserDelete } from '../components/rest-api-calls.js';

class ChangePassword extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            username: "",
            users: props.users,
            password: "",
            errorMessage: "Password field is empty",
        }
    }

    handlePwdChange(e) {
        if (validator.isStrongPassword(e.target.value, {
            minLength: 10, minLowercase: 1,
            minUppercase: 1, minNumbers: 1, minSymbols: 1
        })) {
            this.setState({errorMessage: 'Password OK'});
            this.setState({password: e.target.value});
        } else {
            this.setState({errorMessage: 'Password is not strong enough!'});
            this.setState({password: e.target.value});
        }
    }

    handleChange(e) {
        this.setState({username: e.target.value});
    }

    handleSubmit(e) {
        if(this.state.username === "no user chosen") {
            alert("Choose a user to modify their password!");
            return;
        }

        if(this.state.errorMessage==='Password OK') {
            postUserModification(this.state.username, this.state.password);
        } else {
            alert("Password is not strong enough!");
        }
    }

    render() {
        return (
            <Layout>
                <div className={styles.container}>
                    <h1 className={styles.title}>Change Password of User</h1>
                    <div className={styles.form}>
                        <select name="usernames" id="usernames" value={this.state.username} onChange={this.handleChange.bind(this)}>
                            <option key={-1} value={"no user chosen"}>{"no user chosen"}</option>
                            {
                                this.props.users.map((user) => {
                                    return (
                                        <option key={user.id} value={user.username}>{user.username}</option>
                                    );
                                })
                            }
                        </select>
                        <input className={styles.input} type="password" name="password" placeholder="new password" value={this.state.password} onChange={this.handlePwdChange.bind(this)} required />
                        <button className={styles.btn} onClick={this.handleSubmit.bind(this)}>Change Password</button>
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

class DeleteUser extends React.Component {
    constructor(props) {
        super(props);
        
        this.state = {
            username: "",
            password: "",
            errorMessage: "Password field is empty",
        }
    }

    handleChange(e) {
        this.setState({username: e.target.value});
    }

    handleSubmit(e) {
        if(this.state.username === "no user chosen") {
            alert("Choose a user to deletes!");
            return;
        }

        postUserDelete(this.state.username)
    }

    render() {
        return (
            <Layout>
                <div className={styles.container}>
                    <h1 className={styles.title}>Delete User</h1>
                    <div className={styles.form}>
                        <select name="usernames" id="usernames" value={this.state.username} onChange={this.handleChange.bind(this)}>
                            <option key={-1} value={"no user chosen"}>{"no user chosen"}</option>
                            {
                                this.props.users.map((user) => {
                                    return (
                                        <option key={user.id} value={user.username}>{user.username}</option>
                                    );
                                })
                            }
                        </select>
                        <button className={styles.btn} onClick={this.handleSubmit.bind(this)}>Delete User</button>
                    </div>
                </div>
            </Layout>
        );
    };
}

class AdminPage extends React.Component {
    constructor(props) {
        super(props);
        const users = []

        this.state = {
            users: users,
        }
    }

    async componentDidMount() {
        const users = await getUsers();
        this.setState({
            users: users,
        });
    }

    render() {
        return (
            <div>
                <ThymeHeader />
                <h1>Hi admin</h1>
                <div className="caff-page-row">
                    <div className="column">
                        <ChangePassword 
                            users={this.state.users}
                        />
                    </div>
                    <div className="column">
                        <DeleteUser 
                            users={this.state.users}
                        />
                    </div>
                </div>
            </div>
        );
    }
}
export default AdminPage