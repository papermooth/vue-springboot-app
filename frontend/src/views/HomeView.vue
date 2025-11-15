<template>
  <div class="home">
    <h1>Welcome to Vue3 + Spring Boot App</h1>
    <p>This is a full-stack application with Vue3, Spring Boot, MySQL and Redis.</p>
    <router-link to="/dashboard" class="btn">Go to Dashboard</router-link>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  data() {
    return {
      message: 'Welcome to our application!'
    }
  }
}
</script>

<style scoped>
.home {
  text-align: center;
  padding: 2rem;
}

h1 {
  color: #333;
  margin-bottom: 1rem;
}

.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  margin-top: 1rem;
}

.btn:hover {
  background-color: #3aa776;
}
</style>