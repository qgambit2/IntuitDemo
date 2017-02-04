var postTxtArea;
var searchText;
var Counter = React.createClass({
	name: 'Counter',
	propTypes:{
		count: React.PropTypes.number.isRequired
	},
	render: function(){
		return React.DOM.span(null, 140-this.props.count+' characters remaining.');
	},
	shouldComponentUpdate: function(nextProps, nextState){
		return nextProps.count!=this.props.count;
	}
});
	
TextAreaCounter = React.createClass({
		name: 'TextAreaCounter',
	propTypes:{
		text: React.PropTypes.string,
		defaultValue: React.PropTypes.string
	},
	_textChange: function(ev){
		this.setState({
			text: ev.target.value.length<=140?ev.target.value:this.state.text
		});
	},
	render: function(){
		var counter = React.DOM.h3(
				{style: {'margin-top': '0px'}},
				 React.createElement(Counter,{
				count: this.state.text.length
			}));
		return React.DOM.div(null,
			React.DOM.textarea({
					value: this.state.text,
					style: { height: '200px', width: '280px'},
					onChange: this._textChange,
				}),
				counter
		);
	},
	getDefaultProps: function(){
		return{text: ''};
	},
	getInitialState: function(){
		return {text: this.props.defaultValue};	
	}
});  

SearchText = React.createClass({
		name: 'searchUsers',
	propTypes:{
		text: React.PropTypes.string,
		defaultValue: React.PropTypes.string
	},
	_textChange: function(ev){
		this.setState({
			text: ev.target.value
		});
	},
	componentDidUpdate: function(){
		search(this.state.text);
	},
	render: function(){
		return React.DOM.input({
			value: this.state.text,
			type: 'text',
			style: { width: '120px', marginBottom: '5px'},
			onChange: this._textChange,
		});
	},
	getDefaultProps: function(){
		return{text: ''};
	},
	getInitialState: function(){
		return {text: this.props.defaultValue};	
	}
}); 

$(document).ready(function(){
	postTxtArea = ReactDOM.render(
		React.createElement(TextAreaCounter, {defaultValue:""}), 
		document.getElementById("tweetTextAreaHolder")
	);
	searchText = ReactDOM.render(
		React.createElement(SearchText, {defaultValue:""}), 
		document.getElementById("searchUsersHolder")
	);
});
  