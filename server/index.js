const express = require('express');
const cors = require('cors');
const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());

// ذخیره وضعیت دستگاه‌ها (در حافظه رم، برای پروژه کوچک کافی است)
let devices = {};

// API برای دریافت وضعیت از گوشی
app.post('/update', (req, res) => {
    const { deviceId, battery, location, status } = req.body;
    
    if (deviceId) {
        devices[deviceId] = {
            battery,
            location,
            status,
            lastSeen: new Date()
        };
        console.log(`Device ${deviceId} updated.`);
    }
    res.json({ success: true });
});

// API برای گرفتن لیست تمام دستگاه‌ها (برای پنل ادمین)
app.get('/devices', (req, res) => {
    res.json(devices);
});

app.listen(port, () => {
    console.log(`Spy Server running at http://localhost:${port}`);
});
