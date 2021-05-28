const express = require('express');
const app = express();
const mysql = require("mysql2");
const dbConfig = require('./db.config');
const bodyParser = require('body-parser');
const _port = 8000
const http = require("http")

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

  const connection = mysql.createConnection({
    host: dbConfig.HOST,
    user: dbConfig.USER,
    password: dbConfig.PASSWORD,
    database: dbConfig.DB
  });

app.use(express.static('./res/workHiragana'))
app.use(express.static('./res/workKatakana'))

async function start() {
  try {
    connection.connect((err) => {
        if(err){
            console.log("Ошибка: ", err);
        }
        else
            console.log("mysql connection is stable");
    })
    module.exports = connection;
    app.listen(_port, () => {
      console.log('App has been started on port ', _port);
    });

    /*http.createServer(function (req, res) {
      res.writeHead(200, {'Content-Type': 'text/plain'});
      res.end('Hello World\n');
    }).listen(_port);
    console.log('Server running at ', _port);*/

   /* http.createServer((req,res)=>{
      
    }).listen(_port, "192.168.0.68")*/
  } catch (error) {
    console.log('Server error: ', error);
    process.exit(1);
  }
};

start();

app.get('/', (req,res)=>{
  return res.status(200).json({message: "Добро пожаловать"});
})

app.use('/api/user', require('./routes/user'))

app.use('/api/mods', require('./routes/mode'))

app.use('/api/game', require('./routes/game'))

app.use('/api/records', require('./routes/records'))
