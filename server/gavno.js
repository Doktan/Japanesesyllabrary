import express from 'express';
const app = express()
import { createConnection } from 'mysql2';
import { HOST, USER, PASSWORD, DB } from './db.config';
import { urlencoded, json } from 'body-parser';
import router from './routes/user';


const _port = 8080

app.use(urlencoded({extended: false}))

app.use(json())

const connection = createConnection({
    host: HOST,
    user: USER,
    password: PASSWORD,
    database: DB
  });

  async function start() {
    try {
      connection.connect((err) => {
          if(err){
              console.log("Ошибка: ", err);
          }
          else{
              console.log("mysql connection is stable");
          }
      })
      module.exports = router;
      app.listen(_port, () => {
        console.log('App has been started on port ', _port);
      });
    } catch (error) {
      console.log('Server error: ', error);
      process.exit(1);
    }
  };

  start()

  app.get('/', (req,res)=>{
      return res.status(200).json({message: "Добро пожаловать!"})
  })

  app.get("/biba", (req,res)=>{
    return res.status(200).json({message: "sosi"})
  })

  app.post('/biba',(req,res)=>{
    console.log(req.body)
    return res.status(200).json({message: "Я получил запрос"})
  })

//роут авторизации
app.use('/api/user', import('./routes/user'))
