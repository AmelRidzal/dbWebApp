const idInput = document.getElementById("idInput");
const firstNameInput = document.getElementById("firstNameInput");
const lastNameInput = document.getElementById("lastNameInput");
const phoneInput = document.getElementById("phoneInput");
const output = document.getElementById("output");

const addBtn = document.getElementById("addBtn");
const findBtn = document.getElementById("findBtn");
const deleteBtn = document.getElementById("deleteBtn");
const showBtn = document.getElementById("showBtn");

// Add customer
addBtn.addEventListener("click", async () => {
  const customer = {
    firstName: firstNameInput.value.trim(),
    lastName: lastNameInput.value.trim(),
    phoneNumber: phoneInput.value.trim()
  };

  if (!customer.firstName || !customer.lastName || !customer.phoneNumber) {
    output.value = "⚠️ Fill all fields except ID.";
    return;
  }

  try {
    const res = await fetch("/customers", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(customer)
    });

    const data = await res.json();
    output.value = "✅ Added:\n" + JSON.stringify(data, null, 2);
  } catch (err) {
    output.value = "❌ Error adding customer: " + err;
  }
});

// Find customer by ID
findBtn.addEventListener("click", async () => {
  const id = idInput.value.trim();
  if (!id) {
    output.value = "⚠️ Enter ID.";
    return;
  }

  try {
    const res = await fetch(`/customers/${id}`);
    if (!res.ok) {
      output.value = "❌ Customer not found.";
      return;
    }
    const data = await res.json();
    output.value = "🔍 Found:\n" + JSON.stringify(data, null, 2);
  } catch (err) {
    output.value = "❌ Error finding customer: " + err;
  }
});

// Delete customer by ID
deleteBtn.addEventListener("click", async () => {
  const id = idInput.value.trim();
  if (!id) {
    output.value = "⚠️ Enter ID.";
    return;
  }

  try {
    const res = await fetch(`/customers/${id}`, { method: "DELETE" });
    const text = await res.text();
    output.value = text;
  } catch (err) {
    output.value = "❌ Error deleting customer: " + err;
  }
});

// Show all customers
showBtn.addEventListener("click", async () => {
  try {
    const res = await fetch("/customers");
    const data = await res.json();
    output.value = "📋 All Customers:\n" + JSON.stringify(data, null, 2);
  } catch (err) {
    output.value = "❌ Error loading customers: " + err;
  }
});
