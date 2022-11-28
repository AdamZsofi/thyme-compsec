import React from 'react';
import ThymeHeader from '../components/header.js';
import {CaffFile} from '../components/caff-data.js';
import Link from 'next/link';

function CaffCard(props) {
  return (
    <div className="card" onClick={() => {}}>
      <Link href="/caff">
        <h2>Id (Temporary): {props.caffId}</h2>
        <h2>Name: {props.caffName}</h2>
        <h3>Uploaded by: {props.userName}</h3>
      </Link>
    </div>
  )
}

async function getCaffs() {
  const res = await fetch(`/api/caff`, {
    method: "GET",
  })
  if (res.ok) {
    const json = (await res.json());
    var caffArr = [];
    for(var i in json) {
      caffArr.push(json[i]);
    }
    return caffArr;
  } else {
    // TODO
    return [];
  }
}

class CaffList extends React.Component {
  constructor(props) {
    super(props);
    this.state = { cards: [] }
  }

  render() {
    return (
      <div className="grid-container">
        {this.state.cards}
      </div>
    );
  }

  async componentDidMount() {
    const caffArr = await getCaffs();
    const cards = caffArr.map((file) => {
      return (
        <div className="grid-item" key={file.id}>
          <CaffCard 
            caffId={file.id}
            caffName={"placeholder"}//{file.caffName}
            userName={"placeholder"}//{file.userName}
          />
        </div>
      )
    });
    this.setState({cards: cards});

  }
}

class ThymeHomePage extends React.Component {
  render() {
    return (
      <div>
        <ThymeHeader />
        <CaffList />
      </div>
    )
  }
}

// ========================================

export default ThymeHomePage