const dbUrl = document.getElementById("dbUrl");
const dbName = document.getElementById("dbName");
const dbUser = document.getElementById("dbUser");
const dbPass = document.getElementById("dbPass");
const output = document.getElementById("output");

const saveBtn = document.getElementById("saveBtn");
const testBtn = document.getElementById("testBtn");

// Save DB settings
saveBtn.addEventListener("click", async () => {
    const settings = {
        url: dbUrl.value.trim(),
        name: dbName.value.trim(),
        user: dbUser.value.trim(),
        password: dbPass.value.trim()
    };

    if (!settings.url || !settings.name || !settings.user) {
        output.value = "⚠️ Fill in all fields except password if empty.";
        return;
    }

    try {
        const res = await fetch("/api/db/settings", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(settings)
        });

        const text = await res.text();
        output.value = "✅ " + text;
    } catch (err) {
        output.value = "❌ Error saving settings: " + err;
    }
});

// Test DB connection
testBtn.addEventListener("click", async () => {
    const settings = {
        url: dbUrl.value.trim(),
        name: dbName.value.trim(),
        user: dbUser.value.trim(),
        password: dbPass.value.trim()
    };

    try {
        const res = await fetch("/api/db/test", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(settings)
        });

        const text = await res.text();
        output.value = "🔍 " + text;
    } catch (err) {
        output.value = "❌ Error testing connection: " + err;
    }
});
