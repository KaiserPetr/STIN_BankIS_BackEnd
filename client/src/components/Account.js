import React, { Component } from "react";
import { useState } from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import Select from '@material-ui/core/Select/Select';
import MenuItem from '@material-ui/core/MenuItem/MenuItem';
import FormControl from '@mui/material/FormControl';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Divider from '@mui/material/Divider';
import { styled } from '@mui/material/styles';

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
        maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
        width: 80,
        },
    },
};

const Root = styled('div')(({ theme }) => ({
    width: '100%',
    ...theme.typography.body2,
    '& > :not(style) + :not(style)': {
      marginTop: theme.spacing(0),
    },
  }));

function createTransactionData(id, operation, wrbtr, waers, message) {
    return { id, operation, wrbtr, waers, message };
  }

const operations = ["+","-"]

export default function Account({ clientId, loginCallback }) {
    
    //clientId="1234" //TOTO PAK SMAZAT
    let navigate = useNavigate();
    const [transactions, setTransactions] = useState([]);
    const [checked, setChecked] = React.useState(false);
    const [userData,setUserData] = useState([]);
    const [accountData,setAccountData] = useState([]);
    const [balanceData,setBalanceData] = useState([]);
    const [currency,setCurrency] = useState("");
    const [accountCurrencies,setAccountCurrencies] = useState([]);
    const [allCurrencies,setAllCurrencies] = useState([]);
    const [payID,setPayID] = useState("");
    const [payCurrency,setPayCurrency] = useState("");
    const [payOperator,setPayOperator] = useState("");
    const [payMessage,setPayMessage] = useState("");
    const [payResult,setPayResult] = useState("");
    const [exRateDate,setExRateDate] = useState("");
    const handleLogout = (e) => {
        loginCallback(-1)
        navigate("/")   
    };
    const [payWrbtr, setPayWrbtr] = useState("");

    //toto se vola kazdou minutu
    const MINUTE_MS = 60000;

    React.useEffect(() => {
        const interval = setInterval(() => {
            let today = new Date()
            if (!((today.getDay() === 6) || (today.getDay()  === 0))) {
                let date = today.getDate() + '.' + (today.getMonth() + 1) + '.' + today.getFullYear()
                if (date != exRateDate){
                    if (today.getHours() == 10 && today.getMinutes() >= 20 && today.getMinutes <= 50){
                        axios.get("http://localhost:8081/downloadExchangeRates")
                        .then( res=> {
                            setExRateDate(res.data)
                        })
                    }
                }
            }
        }, MINUTE_MS);
        return () => clearInterval(interval);
      }, [])


    React.useEffect(() => {
        try {
            axios.post("http://localhost:8081/getUserData",clientId)
            .then(res => {
                setUserData(res.data);
            })
            axios.post("http://localhost:8081/getAccountData",clientId)
            .then(res => {
                setAccountData(res.data);
            })
            //TODO toto volat pravidelne
            axios.get("http://localhost:8081/downloadExchangeRates")
            .then( res=> {
                setExRateDate(res.data)
            })
            axios.get("http://localhost:8081/getCurrencies")
            .then(res => {
                setAccountCurrencies(res.data)
                setCurrency(res.data[0])
                setPayCurrency(res.data[0])
            })
            axios.get("http://localhost:8081/getAllCurrencies")
            .then(res => {
                setAllCurrencies([]);
                setAllCurrencies(res.data);
            })
            axios.post("http://localhost:8081/getAccountBalance","CZK")
            .then(res => {
                setBalanceData(res.data);
            })
            axios.get("http://localhost:8081/getTransactions")
            .then(res => {
                setTransactions([])
                res.data.forEach(t => {
                    let newTransaction = createTransactionData(t['id'], t['operation'], t['currency'].wrbtr, t['currency'].waers, t['message'])
                    setTransactions(transactions => [...transactions, newTransaction]);
                })    
            })
        } catch (err) {
            console.log(err)
        }
    }, []);

    const handleSwitchChange = (event) => {
        setChecked(event.target.checked);
      };

    const handlePayWrbtr = (e) => {
        const value = e.target.value.replace(/\D/g, "");
        setPayWrbtr(value);
    };  

    const handlePayCurrencyChange = (e) => {
        setPayCurrency(e.target.value)
    }
    
    const handlePayMessage = (e) => {
        setPayMessage(e.target.value)
    }

    const handlePayOperator = (e) => {
        setPayOperator(e.target.value)
    }

    const handleTransMessage = (e, id) => {
        switch(e){
            case 0:
                setPayResult(`Platba ${id} provedena.`)
                break
            case 1:
                axios.post("http://localhost:8081/getExchangeRate",payCurrency)
                .then(res => {
                    setPayResult(`Platba ${id} provedena. ${payCurrency}:CZK~1:${res.data}`)
                })
                break
            case 2:
                setPayResult("Chyba, nedostatečný zůstatek.")
                break
            case 3:
                setPayResult("Chyba, špatný operátor transakce.")
                break
        }
    }

    const handleCurrencyChange = (e) => {
        setCurrency(e.target.value)
        try {
            axios.post("http://localhost:8081/getAccountBalance",e.target.value)
            .then(res => {
                setBalanceData(res.data);
            })
        } catch (err) {
            console.log(err)
        }
      };

    const handleRndTrans = (e) => {
        handleTransMessage("")
        axios.get("http://localhost:8081/generateRandomTransaction")
            .then(res => {
                setPayID(res.data['id'])
                setPayOperator(res.data['operation'])
                setPayWrbtr(res.data['currency'].wrbtr)
                setPayCurrency(res.data['currency'].waers)
                setPayMessage(res.data['message'])
        })
    };

    const handlePay = (e) => {
        try{
            if (payOperator === "" || payWrbtr === "" || payCurrency === ""){
                setPayResult("Chyba, vyplňte všecha povinná pole.")
            } else {
                axios.get("http://localhost:8081/getNewID")
                .then(resID => {
                    setPayID(resID.data)
                    let params = [resID.data,payOperator,payWrbtr,payCurrency,payMessage]
                
                    axios.post("http://localhost:8081/newTransaction",params)
                    .then(resTrans => { 
                        handleTransMessage(resTrans.data, resID.data)
                        if (resTrans.data == 1 || resTrans.data == 0 ){
                            //refresh transakci
                            axios.get("http://localhost:8081/getTransactions")
                            .then(res => {
                                setTransactions([])
                                res.data.forEach(t => {
                                    let newTransaction = createTransactionData(t['id'], t['operation'], t['currency'].wrbtr, t['currency'].waers, t['message'])
                                    setTransactions(transactions => [...transactions, newTransaction]);
                                })    
                            })
                            //refresh zustatku
                            if (payCurrency == currency){
                                setCurrency(payCurrency)
                                axios.post("http://localhost:8081/getAccountBalance",payCurrency)
                                .then(res => {
                                    setBalanceData(res.data)
                                })
                            } else if (resTrans.data == 1 && currency == "CZK") {
                                axios.post("http://localhost:8081/getAccountBalance","CZK")
                                .then(res => {
                                    setBalanceData(res.data)
                                })
                            }
                        }
                    })
                })
            }
        } catch (err) {
            console.log(err)
        }
    };

    return (
        <Box
          display="block" 
          width={840} height={800} 
          component="form"
          noValidate
          autoComplete="off"
          marginTop={2}
          marginLeft={"auto"}
          marginRight={"auto"}
        > 
          <Paper elevation={6}>
            <Box display="inline-flex">
                <Box
                    m="auto"
                    paddingTop={2}
                    paddingLeft={2}
                    width={680} height={70}
                    textAlign="left"
                >
                    <Typography variant="h4" display="block" gutterBottom> 
                        Správa klientského účtu
                    </Typography>
                </Box>
                <Box
                    m="auto"
                    paddingTop={2}
                    width={40} height={70}
                    textAlign="right"
                >
                    <Button variant="contained" size="large" onClick={handleLogout} 
                    >
                    Odhlásit
                    </Button>
                </Box>
            </Box>
            <Box display="inline-flex" textAlign="center">
                <Box paddingLeft={2} width={80}>
                    <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    ID
                            </Typography>
                        <Divider></Divider>
                            <Typography variant="h6"> 
                                    {userData.id}
                            </Typography>
                        </Root>
                    </Paper>
                </Box>
                <Box paddingLeft={2} width={205}>
                    <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    Číslo účtu
                            </Typography>
                            <Divider></Divider>
                            <Typography variant="h6"> 
                                    {accountData.accountNumber}
                            </Typography>
                        </Root>
                    </Paper>
                </Box>
                <Box paddingLeft={3} width={200}>
                    <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    Vlastník
                            </Typography>
                            <Divider></Divider>
                            <Typography variant="h6"> 
                                    {userData.name} {userData.surname}
                            </Typography>
                        </Root>
                    </Paper>
                </Box>
                <Box paddingLeft={3} width={250}>
                    <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    Email
                            </Typography>
                            <Divider></Divider>
                            <Typography variant="h6"> 
                                    {userData.email}
                            </Typography>
                        </Root>
                    </Paper>
                </Box>
            </Box>
            <Box display="inline-flex" textAlign="center" marginBottom={2}>
            <Box paddingLeft={2} paddingTop={2} width={300}>
            <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    Zůstatek
                            </Typography>
                            <Divider></Divider>
                            <Typography variant="h6"> 
                                    {balanceData.wrbtr}
                            </Typography>
                        </Root>
                    </Paper>
                </Box>
                <Box paddingLeft={3} paddingTop={2} width={200}>
                    <Paper elevation={4}>
                        <Root>
                            <Typography variant="h6"> 
                                    Měna
                            </Typography>
                            <Divider></Divider>
                            <FormControl sx={{ width: 80}} size="small">
                        
                                <Select
                                value={currency}
                                onChange={handleCurrencyChange}
                                //input={<OutlinedInput label="" />}
                                MenuProps={MenuProps}
                                >
                                {accountCurrencies.map((name) => (
                                    <MenuItem
                                    key={name}
                                    value={name}
                                    >
                                    {name}
                                    </MenuItem>
                                ))}
                                </Select>
                            </FormControl>
                        </Root>
                    </Paper>
                </Box>
                <Box paddingLeft={5} width={483} paddingTop={3.5}>
                    <FormGroup>
                        <FormControlLabel control={<Switch onChange={handleSwitchChange} />} label="Zobrazit menu platby" />
                    </FormGroup>
                </Box>
            </Box>
            </Paper>
            { checked && 
                <Paper elevation={6} style={{
                marginTop: 5,
                }}>
                <Box
                    paddingTop={1}
                    paddingLeft={2}
                    width={680} height={25}
                    textAlign="left"
                >
                    <Typography variant="h6" display="block" gutterBottom> 
                        Nová platba
                    </Typography>
                </Box>
                <Box display="inline-flex" textAlign="left" margin={2}>

                        <FormControl sx={{ width: 80}} size="small">
                            <InputLabel>Operátor</InputLabel>
                            <Select
                            value={payOperator}
                            onChange={handlePayOperator}
                            input={<OutlinedInput label="Operator" />}
                            MenuProps={MenuProps}
                            >
                            {operations.map((name) => (
                                <MenuItem
                                key={name}
                                value={name}
                                >
                                {name}
                                </MenuItem>
                            ))}
                            </Select>
                        </FormControl>

                        <TextField id="outlined-basic" label="Částka" variant="outlined" size="small"
                        sx={{input: {textAlign: "left"  }, marginLeft: 1}}
                        value={payWrbtr}
                        onChange={handlePayWrbtr}
                        />

                        <FormControl sx={{ width: 80, marginLeft: 1}} size="small">
                            <InputLabel>Měna</InputLabel>
                            <Select
                            value={payCurrency}
                            onChange={handlePayCurrencyChange}
                            input={<OutlinedInput label="Měna" />}
                            MenuProps={MenuProps}
                            >
                            {allCurrencies.map((name) => (
                                <MenuItem
                                key={name}
                                value={name}
                                >
                                {name}
                                </MenuItem>
                            ))}
                            </Select>
                        </FormControl>

                        <TextField id="outlined-basic" label="Zpráva" variant="outlined" size="small"
                        sx={{input: {textAlign: "left"}, width: 500, marginLeft: 1}}
                        value={payMessage}
                        onChange={handlePayMessage}
                        />
                    
                </Box>
                <Box
                    display="inline-block"
                    paddingLeft={2}
                    width={800} height={50}
                    textAlign="left"
                >
                    <Button variant="contained" color="success" onClick={handlePay}
                    >
                    Zaplatit
                    </Button>

                    <Button variant="contained" sx={{ marginLeft: 1 } } onClick={handleRndTrans}
                    >
                    Náhodná platba
                    </Button>
                    
                    <Box display="inline-block" paddingLeft={11} textAlign="left">
                        <Typography variant="h6"> 
                            {payResult}
                        </Typography>
                    </Box>
                </Box>
                </Paper>
            }
          <Paper elevation={6} style={{
            marginTop: 5,
            }}>
            <Box display="inline-flex" textAlign="left" marginBottom={1} paddingLeft ={2} paddingTop ={2}>
                <Typography variant="h5" display="block" gutterBottom> 
                        Pohyby
                </Typography>
            </Box>
            <Box paddingLeft ={2} paddingRight ={2} paddingBottom ={2}>
                <TableContainer component={Paper}>
                    <Table sx={{ minWidth: 650 }} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell align="left" sx={{ fontWeight: 'bold', width: '5%'}}>ID</TableCell>
                                <TableCell align="left" sx={{ fontWeight: 'bold', width: '5%'}}>Operace</TableCell>
                                <TableCell align="left" sx={{ fontWeight: 'bold', width: '20%'}}>Částka</TableCell>
                                <TableCell align="left" sx={{ fontWeight: 'bold', width: '5%'}}>Měna</TableCell>
                                <TableCell align="left" sx={{ fontWeight: 'bold'}}>Zpráva</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                        {transactions.map((row) => (
                            <TableRow
                            key={row.id}
                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                            <TableCell component="th" scope="row">{row.id}</TableCell>
                            <TableCell align="center">{row.operation}</TableCell>
                            <TableCell align="left">{row.wrbtr}</TableCell>
                            <TableCell align="left">{row.waers}</TableCell>
                            <TableCell align="left">{row.message}</TableCell>
                            </TableRow>
                        ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
          </Paper>
        </Box>
    )
}