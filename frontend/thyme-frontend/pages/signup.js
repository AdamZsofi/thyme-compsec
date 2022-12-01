import { useRouter } from "next/router"
import { useState } from "react"

import { postUserRegistration } from './rest-api-calls.js';
import Layout from "../components/layout"
import styles from "../styles/signin.module.css"
import validator from 'validator'
import Link from 'next/link.js';

// TODO do we want client side hashing?
// source: https://www.fullstackbook.com/code/nextjs-jwt/
export default function SignIn() {
  // TODO not used in login, should be used in any other not GET
  // get csrf token, source: https://devdojo.com/ketonemaniac/doing-spring-securitys-csrf-tokens-the-right-way-with-react

  const router = useRouter()

  const [state, setState] = useState({
    username: "",
    password: "",
    errorMessage: "Password field is empty",
  })

  function handleChange(e) {
    const copy = { ...state }
    copy[e.target.name] = e.target.value
    setState(copy)
  }

  function handlePwdChange(e) {
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

  async function handleSubmit() {
    postUserRegistration(state.username, state.password);
  }

  return (
    <Layout>
      <div className={styles.container}>
        <h1 className={styles.title}>Sign Up</h1>
        <div className={styles.form}>
          <input className={styles.input} type="text" name="username" placeholder="username" value={state.username} onChange={handleChange} required />
          <input className={styles.input} type="password" name="password" placeholder="password" value={state.password} onChange={handlePwdChange} required />
          <button className={styles.btn} onClick={handleSubmit}>Submit</button>
        </div>
      </div>
      {state.errorMessage === '' ? null :
        <span style={{
          fontWeight: 'bold',
          color: 'red',
        }}>{state.errorMessage}</span>}

      <div id="message">
        <h3>Password must contain the following:</h3>
        <p id="letter" className="invalid">A <b>lowercase</b> letter</p>
        <p id="capital" className="invalid">A <b>capital (uppercase)</b> letter</p>
        <p id="number" className="invalid">A <b>number</b></p>
        <p id="number" className="invalid">A <b>symbol</b></p>
        <p id="length" className="invalid">Minimum <b>10 characters</b></p>
      </div>
      <Link href="/signin">Already a user? Sign in here!</Link>
    </Layout>
  )
}