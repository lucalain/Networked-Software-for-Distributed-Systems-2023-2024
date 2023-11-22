/*
Create a generic object (class) representing an entry in an address book
— Prepare a proper constructor that accounts 
for name,address, civic number, and zip code
Ask the user for a name, a street, a civic
number, and a zip code;
then create an object
Use a method to display the information on the console
*/
const prompt = require('prompt-sync')({sigint: true});

class User {
    constructor(name, address, civic, zip) {
        this.name = name;
        this.address = address;
        this.civic = civic;
        this.zip = zip;
        this.showInfo = function () {
            console.log("Name:" + this.name + " Address:" + this.address +" Civic:"+ this.civic + " Zip:"+this.zip);
        };
    }
}

let name = prompt("Insert name:");
let address = prompt("Insert address:");
let civic = prompt("Insert civic:");
let zip = prompt("Insert zip:");
let u = new User(name, address, civic,zip);
u.showInfo();

