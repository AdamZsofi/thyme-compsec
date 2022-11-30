import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import logo from "/public/thyme.png"

function SearchBar(props) {
  return (
    <div className="searchContainer">
      <form>
        <button type="submit">Submit</button>
        <input type="text" placeholder="Search.."></input>
      </form>
    </div>
  )
}

function logOutClick() {
  router.push({
    pathname: '/logout'
  });    
}
  
function ThymeHeader(props) {
  return (
    <div className="header">
      <Link href="/" className="logo">
        <Image width={500} height={70} className="logoImg" src={logo} alt="Thyme Shop Logo"/>
        <span className="shopName">Thyme Shop</span>
      </Link>
      <div className="header-right">
        <SearchBar />
        <button onClick={logOutClick}>Logout</button>
      </div>
    </div>
  );
}
  
export default ThymeHeader;