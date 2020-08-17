# SMS-controlled Minecraft server 📱 ➡️ ⛏💎

This project was built [on-stream](https://twitch.tv/MaximumGilliard). It's a Java application that starts a Minecraft server and a webserver. The webserver is designed to handle webhook requests from Twilio's [Programmable SMS API](https://www.twilio.com/docs/usage/webhooks/sms-webhooks) and forward the SMS body as a command to the MC server.

## How to run

(remember, this is a work-in-progress!).

### Minecraft server setup

  - Download the minecraft server jar from [https://www.minecraft.net/en-us/download/server/](https://www.minecraft.net/en-us/download/server/).
  - Run it by hand: `java -jar server.jar`. You will get a message about the EULA.
  - Read the EULA then change the `eula.txt` file to indicate that you've read and understood it.

### Twilio setup

  - Create a Twilio account and buy a phone number: https://twilio.com/try-twilio
  - Use `ngrok` [from here](https://ngrok.com) to create a public URL for `localhost:4567`:  `ngrok http 4567`
  - Copy the ngrok forwarding url (better use the HTTPS one), and set the webhook URL for your Twilio number to `<YOUR NGROK URL>/mc`
  
### Application setup

  - Clone the repo and load it into your IDE
  - Check the path for the Minecraft server is set correctly [here](https://github.com/mjg123/MinecraftBySMS/blob/master/src/main/java/McServerController.java#L88).
  - Run the main method in your IDE

Run Minecraft game client locally and connect to a server on `localhost:25565`.
Send SMS to your Twilio number with Minecraft server commands, eg `say Hello everyone` or `weather clear`. There are [loads of things](https://minecraft.gamepedia.com/Commands#List_and_summary_of_commands) you can do.

🎉🎉 ENJOY 🎉🎉
