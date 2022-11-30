import React from 'react';
import ThymeHeader from '../components/header.js';

function handleLogout() {
    alert("TODO");
    router.push({
        pathname: '/signin'
    });
}

function LogoutPage(props) {
    return (
        <div>
            <button onClick={handleLogout}>Click here to log out</button>
        </div>
    )
}

export default LogoutPage