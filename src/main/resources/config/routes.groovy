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
	post '/createOrder'
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