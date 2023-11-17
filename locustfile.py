from locust import HttpUser, task


class Swarmer(HttpUser):
    @task
    def register(self):
        self.client.get("http://localhost:16356/api/register")
