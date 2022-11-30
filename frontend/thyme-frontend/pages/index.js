import React from 'react';
import ThymeHeader from '../components/header.js';
import CaffList from '../components/caff-card.js'

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
        <CaffList 
          caffArr={this.state.caffArr}
        />
      </div>
    )
  }
}

// ========================================

export default ThymeHomePage