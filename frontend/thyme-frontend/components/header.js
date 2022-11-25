import React from 'react';

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
        <a href="/" className="logo">
          <img className="logoImg" src="thyme.png" alt="Thyme Shop Logo"/>
          <span className="shopName">Thyme Shop</span>
        </a>
        <div className="header-right">
          <SearchBar />
        </div>
      </div>
    );
  }
  
export default ThymeHeader;