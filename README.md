# 🦊 Fennec

    Afraid of accidentally making your IP-Address public due to unsafe Docker Configurations?
    Fear not! Fennec is here to help you.

---
Configure Fennec to check Docker Containers for IP Leaks. Whenever Fennec detects a potential IP Leak, it will notify you.
You can even configure Fennec to automatically stop or kill the container in question.

Currently, you'll need to build the Docker Image yourself!
Fennec needs access to the Docker Socket to check the Containers for IP Leaks.

Use ``gradle dockerCreateDockerfile`` to create a Dockerfile

Use ``docker build build/docker -t fennec:latest`` to build the Docker Image


---

Environment Variables:

| ENV                    | Description                                            | Value   |
|------------------------|--------------------------------------------------------|---------|
| DOCKER_CONTAINER_IDS   | List of Container IDs to check seperated by commas (,) | String  |
| STOP_ON_TRUE_IP_MATCH  | Stop the Container gracefully if the public IP matches | Boolean |
| KILL_ON_TRUE_IP_MATCH  | Kill the Container if the public IP matches            | Boolean |
| CHECK_INTERVAL         | Duration between IP Checks in Milliseconds             | Long    |
| NOTIFY_EMAIL           | Send Notifications via E-Mail                          | Boolean |
| EMAIL_SMTP_HOST        | E-Mail SMTP Host                                       | String  |
| EMAIL_SMTP_PORT        | E-Mail SMTP Port                                       | Integer |
| EMAIL_SMTP_USERNAME    | E-Mail SMTP Username                                   | String  |
| EMAIL_SMTP_PASSWORD    | E-Mail SMTP Password for User                          | String  |
| EMAIL_FROM_ADDRESS     | E-Mail FROM Address                                    | String  |
| EMAIL_FROM_NAME        | E-Mail FROM Name                                       | String  |
| EMAIL_TO_ADDRESS       | List of E-Mail TO Addresses seperated by commas (,)    | String  |
| NOTIFY_DISCORD_WEBHOOK | Send Notifications via a Discord Webhook (WIP)         | Boolean |
| DISCORD_WEBHOOK_URL    | Discord Webhook URL (WIP)                              | String  |

---
Used:

<a href="https://www.flaticon.com/free-icons/fennec" title="fennec icons">Fennec icons created by Freepik - Flaticon</a>