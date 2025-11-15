<template>
  <div class="about">
    <h1>About This Project</h1>
    <div class="tech-stack">
      <h2>Technology Stack</h2>
      <ul>
        <li><strong>Frontend:</strong> Vue 3, Vue Router, Axios</li>
        <li><strong>Backend:</strong> Spring Boot 3, Java 17</li>
        <li><strong>Database:</strong> MySQL (Master-Slave Cluster)</li>
        <li><strong>Cache:</strong> Redis (Cluster)</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AboutView'
}
</script>

<style scoped>
.about {
  padding: 2rem;
}

.tech-stack {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 4px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 0.5rem 0;
}
</style>