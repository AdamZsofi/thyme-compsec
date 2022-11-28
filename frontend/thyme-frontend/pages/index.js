import React from 'react';
import ThymeHeader from '../components/header.js';
import {CaffFile} from '../components/caff-data.js';
import Link from 'next/link.js';

function CaffCard(props) {
  return (
    <div className="card" onClick={() => {}}>
      <Link href={"/caff/"+props.caffId}>
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
    for(var i in json.caffs) {
      caffArr.push(json.caffs[i]);
    }
    return caffArr;
  } else {
    // TODO this works?
    return [];
  }
}

class CaffList extends React.Component {
  constructor(props) {
    super(props);
    this.state = { caffArr: [] }
  }

  render() {
    const cards = this.state.caffArr.map((file) => {
      return (
        <div className="grid-item" key={file.id}>
          <CaffCard 
            caffId={file.id}
            caffName={file.filename}
            userName={file.username}
          />
        </div>
      )
    });

    return (
      <div className="grid-container">
        {cards}
      </div>
    );
  }

  async componentDidMount() {
    const caffArr = await getCaffs();
    this.setState({caffArr: caffArr});
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