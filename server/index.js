const express = require('express');
const app = express();
const mysql = require("mysql2");
const dbConfig = require('./db.config');
const bodyParser = require('body-parser');
const _port = 8080

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
  } catch (error) {
    console.log('Server error: ', error);
    process.exit(1);
  }
};

start();

app.get('/', (req,res)=>{
  return res.status(200).json({message: "Добро пожаловать"});
})

app.get('/test',(req,res)=>{
  res.status(200).sendFile('F:/everything/AnrdoidApps/Japanesesyllabrary/server/res/workHiragana/h_a.png')
})

app.use('/api/user', require('./routes/user'))

app.use('/api/mods', require('./routes/mode'))

app.use('/api/game', require('./routes/game'))

app.use('/api/records', require('./routes/records'))
