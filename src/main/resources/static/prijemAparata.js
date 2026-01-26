const idInput = document.getElementById("idInput");
const nameInput = document.getElementById("nameInput");
const phoneInput = document.getElementById("phoneInput");
const dateInput = document.getElementById("dateInput");
const problemInput = document.getElementById("problemInput");
const output = document.getElementById("output");

const saveBtn = document.getElementById("saveBtn");
const updateBtn = document.getElementById("updateBtn");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const addBtn = document.getElementById("addBtn");

let customers = [];
let index = 0;

// Load all customers
async function loadCustomers() {
  try {
    const res = await fetch("/customers");
    customers = await res.json();
    if (customers.length > 0) {
      index = customers.length - 1;
      showCustomer(index);
    }

  } catch {
    output.value = "Error loading customers.";
  }
}

function showCustomer(i) {
  const c = customers[i];
  if (!c) return;
  idInput.value = c.id || "";
  nameInput.value = c.name || "";
  phoneInput.value = c.phoneNumber || "";
  dateInput.value = c.dateCreated || "";
  problemInput.value = c.problemDescription || "";
}

// Add new customer
addBtn.addEventListener("click", () => {
  idInput.value = "";
  nameInput.value = "";
  phoneInput.value = "";
  problemInput.value = "";

  // Set today as default date
  const today = new Date().toISOString().split("T")[0];
  dateInput.value = today;

  output.value = "Ready to add new customer.";
});


// Save new customer
saveBtn.addEventListener("click", async () => {
  const customer = {
    name: nameInput.value,
    phoneNumber: phoneInput.value,
    dateCreated: dateInput.value,
    problemDescription: problemInput.value
  };

  try {
    const res = await fetch("/customers", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(customer)
    });
    const data = await res.json();
    output.value = "Saved:\n" + JSON.stringify(data, null, 2);
    loadCustomers();
  } catch {
    output.value = "Error saving customer.";
  }
});

// Update customer
updateBtn.addEventListener("click", async () => {
  const id = idInput.value;
  if (!id) {
    output.value = "Enter ID to update.";
    return;
  }

  const customer = {
    name: nameInput.value,
    phoneNumber: phoneInput.value,
    dateCreated: dateInput.value,
    problemDescription: problemInput.value
  };

  try {
    const res = await fetch(`/customers/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(customer)
    });
    const data = await res.json();
    output.value = "Updated:\n" + JSON.stringify(data, null, 2);
    loadCustomers();
  } catch {
    output.value = "Error updating customer.";
  }
});

// Previous
prevBtn.addEventListener("click", () => {
  if (index > 0) {
    index--;
    showCustomer(index);
  }
});

// Next
nextBtn.addEventListener("click", () => {
  if (index < customers.length - 1) {
    index++;
    showCustomer(index);
  }
});

loadCustomers();


function printPrijem() {
    // Read values from EXISTING inputs
    const name = nameInput.value;
    const phone = phoneInput.value;
    const date = dateInput.value;
    const problem = problemInput.value;

    // Fill print section
    document.getElementById("p_name").innerText = name;
    document.getElementById("p_phone").innerText = phone;
    document.getElementById("p_date").innerText = date;
    document.getElementById("p_problem").innerText = problem;

    // Print only the section
    const printContent = document.getElementById("printSection").innerHTML;
    const originalContent = document.body.innerHTML;

    document.body.innerHTML = printContent;
    window.print();
    document.body.innerHTML = originalContent;

    location.reload();
}

