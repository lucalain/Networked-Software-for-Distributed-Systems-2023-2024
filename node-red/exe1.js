
/*
Write a JavaScript program that asks the user
to input his birthday and computes the age in
number of days
*/

const prompt = require('prompt-sync')({sigint: true});

let day = prompt("insert day: ")
let month = prompt("insert month: ")
let year = prompt("insert year: ")

// JavaScript counts months from 0 to 11
let birthdate = new Date(year, month-1, day);

const giorni = (Date.now() - birthdate) / (1000*60*60*24)
console.log(giorni);
