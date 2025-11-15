<template>
  <div class="app">
    <nav class="navbar">
      <div class="logo">Vue-Spring Boot App</div>
      <div class="nav-links">
        <router-link to="/" class="nav-link">Home</router-link>
        <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
        <router-link to="/about" class="nav-link">About</router-link>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
    <footer class="footer">
      <p>&copy; {{ new Date().getFullYear() }} Vue3 + Spring Boot Application</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  color: #333;
  background-color: #f8f9fa;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.navbar {
  background-color: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: #34495e;
}

.main-content {
  flex: 1;
  background-color: #f8f9fa;
}

.footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 1rem;
  margin-top: auto;
}
</style>
