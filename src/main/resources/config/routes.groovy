package config;

group '/api/user', {
	get '/balances'
	get '/info'
	get '/addresses'
	get '/debug'
	get '/withdrawals'
	get '/deposits'
}, [
	action: 'user',
	middleware: 'auth'
]

group '/api/market', {
	get '/activeOrders'
	get '/completedOrders'
	get '/details'
	post '/createOrder'
	post '/cancelOrder'
}, [
	action: 'market',
	middleware: 'auth'
]

group '/api/auth', {
	post '/validate'
	post '/register'
	post '/login'
	post '/logout'
}, [
	action: 'auth'
]