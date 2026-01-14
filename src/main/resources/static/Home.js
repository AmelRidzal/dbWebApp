// Button references
const prijemBtn = document.getElementById("btnPrijem");
const sqlBtn = document.getElementById("btnSQL");
const settingsBtn = document.getElementById("btnSettings");

// Add actions for navigation or logic
prijemBtn.addEventListener("click", () => {
  console.log("Prijem Aparata clicked");
  window.location.href = "/prijemAparata"; // Example navigation
});

sqlBtn.addEventListener("click", () => {
  console.log("User Manager clicked");
  window.location.href = "/dbManager";
});

settingsBtn.addEventListener("click", () => {
  console.log("DB Settings clicked");
   window.location.href = "/dbSettings";
});