import React from 'react';
import Link from 'next/link';

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
  
  function ThymeHeader(props) {
    return (
      <div className="header">
        <Link href="/" className="logo">
          <img className="logoImg" src="thyme.png" alt="Thyme Shop Logo"/>
          <span className="shopName">Thyme Shop</span>
        </Link>
        <div className="header-right">
          <SearchBar />
        </div>
      </div>
    );
  }
  
export default ThymeHeader;