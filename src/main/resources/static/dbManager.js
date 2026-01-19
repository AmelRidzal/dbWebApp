const input = document.getElementById("input");
const output = document.getElementById("output");
const sendBtn = document.getElementById("sendBtn");

sendBtn.addEventListener("click", async () => {
  const query = input.value.trim();

  if (!query) {
    output.value = "⚠️ Enter a query first.";
    return;
  }

  try {
    const res = await fetch("/customers/query", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ query })
    });


    const data = await res.text();
    output.value = data;
  } catch (err) {
    output.value = "❌ Error sending query: " + err;
  }
});
