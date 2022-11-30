import React from 'react';
import ThymeHeader from '../components/header.js';
import CaffList from '../components/caff-card.js'

import { withRouter } from 'next/router'
import { getSearchResult } from '../components/rest-api-calls.js'

export default withRouter(class SearchResultPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = { caffArr: [] }
  }

  async componentDidMount() {
    const caffArr = await getSearchResult();
    this.setState({caffArr: caffArr});
  }

  render() {
    const key = this.props.router.query.key

    return (
      <div>
        <ThymeHeader />
        <h1>Search Results</h1>
        <h2>You searched for "{key}"</h2>
        <CaffList 
          caffArr={this.state.caffArr}
        />
      </div>
    )
  }
})