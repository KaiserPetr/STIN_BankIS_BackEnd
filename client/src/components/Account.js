import React, { Component } from "react";
import { useState } from 'react';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import Select from '@material-ui/core/Select/Select';
import MenuItem from '@material-ui/core/MenuItem/MenuItem';
import FormControl from '@mui/material/FormControl';

export default function Account({ clientId, loginCallback }) {
    
    //clientId="1234"
    let navigate = useNavigate();
    const [userData,setUserData] = useState([]);
    const [accountData,setAccountData] = useState([]);
    const [balanceData,setBalanceData] = useState([]);
    const [currency,setCurrency] = useState("");
    const handleLogout = (e) => {
        loginCallback(-1)
        navigate("/")   
    };

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
            axios.post("http://localhost:8081/getAccountBalance","CZK")
            .then(res => {
                setBalanceData(res.data);
            })
        } catch (err) {
            console.log(err)
        }
    }, []);

    const handleCurrencyChange = (e) => {
        setCurrency(e.target.value)
        try {
            axios.post("http://localhost:8081/getAccountBalance",e.target.value)
            .then(res => {
                if (res.data.wrbtr === -1){
                    setBalanceData({waers: "EUR", wrbtr: "no-data"})
                } else {
                    setBalanceData(res.data);
                }
            })
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
                <Box paddingLeft={2} width={160}>
                    <Paper elevation={4}>
                        <Typography variant="h6" display="block" gutterBottom> 
                                ID: {userData.id}
                        </Typography>
                    </Paper>
                </Box>
                <Box paddingLeft={3} width={300}>
                    <Paper elevation={4}>
                        <Typography variant="h6" display="block" gutterBottom> 
                                Vlastník: {userData.name} {userData.surname}
                        </Typography>
                    </Paper>
                </Box>
                <Box paddingLeft={3} width={300}>
                    <Paper elevation={4}>
                        <Typography variant="h6" display="block" gutterBottom> 
                                Email: {userData.email}
                        </Typography>
                    </Paper>
                </Box>
            </Box>
            <Box display="inline-flex" textAlign="left" marginBottom={1}>
                <Box paddingLeft={2} paddingTop={1} width={483}>
                    <Paper elevation={4}>
                        <Typography variant="h6" display="block" gutterBottom paddingLeft={1}> 
                                Číslo účtu: {accountData.accountNumber}
                        </Typography>
                    </Paper>
                </Box>
                <Box paddingLeft={2} width={483} paddingTop={0.5}>
                    <Button variant="contained" color="success" size="large">
                    Platba
                    </Button>
                </Box>
            </Box>
          </Paper>
          <Paper elevation={6} style={{
            marginTop: 5,
            }}>
            <Box display="inline-flex" textAlign="left" marginBottom={1}>
                <Box paddingLeft={2} paddingTop={1} width={483}>
                    <Paper elevation={4}>
                        <Typography variant="h6" display="block" gutterBottom paddingLeft={1}> 
                                Zůstatek: {balanceData.wrbtr}
                        </Typography>
                    </Paper>
                </Box>
                <Box paddingLeft={2} paddingTop={1} width={160}>
                <FormControl sx={{ m: 1, minWidth: 80 }}>
                <Select 
                    labelId="demo-simple-select-autowidth-label"
                    label="Měna" defaultValue={"CZK"} onChange={handleCurrencyChange}>
                    <MenuItem value={"CZK"}>CZK</MenuItem>
                    <MenuItem value={"EUR"}>EUR</MenuItem>
                    <MenuItem value={"USD"}>USD</MenuItem>
                </Select>
                </FormControl>
                </Box>
            </Box>
          </Paper>
        </Box>
    )
}