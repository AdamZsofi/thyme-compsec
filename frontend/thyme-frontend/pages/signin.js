import { useRouter } from "next/router"
import { useState } from "react"

import { postUserLogin } from '../components/rest-api-calls.js';
import Layout from "../components/layout"
import styles from "../styles/signin.module.css"
import Link from 'next/link.js';

// TODO do we want client side hashing?
// source: https://www.fullstackbook.com/code/nextjs-jwt/
export default function SignIn() {
  // TODO not used in login, should be used in any other not GET
  // get csrf token, source: https://devdojo.com/ketonemaniac/doing-spring-securitys-csrf-tokens-the-right-way-with-react

  const router = useRouter()

  const [state, setState] = useState({
    username: "",
    password: ""
  })

  function handleChange(e) {
    const copy = { ...state }
    copy[e.target.name] = e.target.value
    setState(copy)
  }

  async function handleSubmit() {
    postUserLogin(state.username, state.password, router);
  }

  return (
    <Layout>
      <div className={styles.container}>
        <h1 className={styles.title}>Sign In</h1>
        <div className={styles.form}>
          <input className={styles.input} type="text" name="username" placeholder="username" value={state.username} onChange={handleChange} required />
          <input className={styles.input} type="password" name="password" placeholder="password" value={state.password} onChange={handleChange} required />
          <button className={styles.btn} onClick={handleSubmit}>Submit</button>
        </div>
        <Link href="/signup">Not a user yet? Sign up here!</Link>
      </div>
    </Layout>
  )
}