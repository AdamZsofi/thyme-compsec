import React from 'react';
import ThymeHeader from '../components/header.js';
import CaffList from '../components/caff-card.js'
import Link from 'next/link';

import { getCaffs } from '../components/rest-api-calls.js'

class ThymeHomePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = { caffArr: [] }
  }

  async componentDidMount() {
    const caffArr = await getCaffs();
    this.setState({caffArr: caffArr});
  }

  render() {
    return (
      <div>
        <ThymeHeader />
        <Link href="/upload">
          <button className='upload-btn'>Upload CAFF</button>
        </Link>
        <br />
        <h1>Files for sale:</h1>
        <CaffList 
          caffArr={this.state.caffArr}
        />
      </div>
    )
  }
}

// ========================================

export default ThymeHomePage