import React, { Component } from 'react'
import './App.css'
import axios from 'axios'

class App extends Component {

  constructor () {
    super()
    this.state = {
      message: 'init'
    }

    this.handleClick = this.handleClick.bind(this)
  }

  handleClick () {
    axios.get('http://localhost:8081/')
    .then(response => this.setState({message: response.data}))
  }

  render () {
      return (
        <div className='button__container' onClick={this.handleClick}>
          <button className='button'>Click me</button>
          <p>{this.state.message}</p>
        </div>
      )
  }
}
export default App