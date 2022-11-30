import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import logo from "/public/thyme.png"

import { useRouter } from 'next/router';
import { useState } from "react"

function SearchBar(props) {
  const [state, setState] = useState({
    key: ''
  })
  
  function handleChange(e) {
    const copy = { ...state }
    copy[e.target.name] = e.target.value
    setState(copy)
  }

  return (
    <div className="searchContainer">
      <form>
        <button type="submit" formAction="/search">Submit</button>
        <input type="text" name="key" placeholder="Search.." onChange={handleChange} value={state.key}></input>
      </form>
    </div>
  )
}

function logOutClick(router) {
  router.push({
    pathname: '/logout'
  });    
}

function ThymeHeader(props) {
  const router= useRouter();

  return (
    <div className="header">
      <Link href="/" className="logo">
        <Image width={500} height={70} className="logoImg" src={logo} alt="Thyme Shop Logo"/>
        <span className="shopName">Thyme Shop</span>
      </Link>
      <div className="header-right">
        <SearchBar />
        <button onClick={() => logOutClick(router)}>Logout</button>
      </div>
    </div>
  );
}
  
export default ThymeHeader;