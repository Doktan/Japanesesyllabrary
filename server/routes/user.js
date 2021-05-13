const { Router } = require("express")
const router = Router()
const connection = require("../index.js")
const bcrypt = require('bcrypt')
//const e = require("express")
const saltRounds = 10

function isEmpty(obj) {
    return Object.keys(obj).length === 0;
}

//api/user

router.post('/register', async (req, res) => {
    console.log("registration data ", req.body)
    const body = req.body
    // name and password
    const query = 'INSERT into `users` (`name`,`password`) VALUES (?,?)'
    const hashedPassword = await bcrypt.hash(body.password, saltRounds)
    const bruh = 'SELECT `name` from `users` where `name` = ?'
    connection.query(bruh, body.name, (err, result) => {
        if (err) {

            console.log('Ошибка ', err)
            return res.status(500).json({ message: "Ошибка сервера" })
        }
        else {

            if (isEmpty(result)) {
                connection.query(query, [body.name, hashedPassword], (errs, results, fields) => {
                    if (errs) {
                        console.log(errs)
                        return res.status(500).json({ message: "Ошибка запроса" })
                    } else {
                        return res.status(200).json({ message: "Registration is successful", data: [hashedPassword] })
                    }
                })
            }
            else {
                res.status(200).json({ message: "Already reigstered!" })
            }
        }
    })
})

router.post('/checkUser', (req, res) => {
    const body = req.body;
    if(isEmpty(body)){
        return res.status(500).json({message: "no data"})
    }
    else{
        if(body.name === '' || body.password === ''){
            return res.status(500).json({message: "corrupted data"})
        }
        else{
            const query = 'SELECT name, password from `users` where name = ?';
            connection.query(query, [body.name, body.password], (err,result)=>{
                if(err){
                    console.log('ошибка check ', err)
                    return res.status(500).json({message: 'server error'})
                }
                else{
                    if(isEmpty(result)){
                        console.log('данные неверные (запрос не дал резов)')
                        res.status(200).json({message: 'no such user'})
                    }else{
                        if(body.name === result[0].name && body.password === result[0].password)
                        {
                            console.log("user found")
                            res.status(200).json({message: 'user found!'})
                        }else{
                            console.log('данные не верные (неправильный логин или пароль)')
                            res.status(200).json({message: "no such user"})
                        }
                    }
                }
            })
        }
    }
})

router.post('/signIn', (req, res) => {
    const body = req.body
    console.log(body)
    const query = 'SELECT name, password from `users` where name = ?'
    if (isEmpty(body))
        return res.status(200).json({ message: 'Input Username and Password!' })
    else
        if (body.name === '' || body.password === '')
            return res.status(200).json({ message: 'Input Username and Password' })
        else {
            connection.query(query, [body.name], async (err, result) => {
                if (err) {
                    console.log('ошибка авторизации, ', err)
                    return res.status(200).json({ message: 'There is no such user or password is incorrect!' })
                } else {
                    console.log(result)
                    if (isEmpty(result))
                        return res.status(200).json({ message: 'There is no such user or password is incorrect!' })
                    else {
                        let passCheck = await bcrypt.compare(body.password, result[0].password) // мог ошибиться UOD - ОШИБКИ НЕТ
                        if (passCheck) {
                            console.log('passCheck is ok')
                            return res.status(200).json({ message: 'Authorized!', data: [result[0].password] })
                        }
                        else {
                            console.log('passCheck is not ok')
                            return res.status(200).json({ message: 'There is no such user or password is incorrect!' })
                        }
                    }

                }
            })
        }

})

router.get('/get', (req, res) => {
    const query = 'SELECT * FROM `users`';
    connection.query(query, (err, result, fileds) => {
        console.log(result)
        console.log("  test ")
        
        return res.status(200).send(result);
    });
});

module.exports = router;
