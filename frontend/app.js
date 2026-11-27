const chatForm = document.querySelector("#chat-form");
const messageInput = document.querySelector("#message-input");
const messages = document.querySelector("#messages");
const sendButton = document.querySelector("#send-button");
const newChatButton = document.querySelector("#new-chat-button");
const statusMessage = document.querySelector("#status-message");

function addMessage(role, text) {
    const row = document.createElement("article");
    row.className = `message-row ${role}`;

    const avatar = document.createElement("div");
    avatar.className = "avatar";
    avatar.setAttribute("aria-hidden", "true");
    avatar.textContent = role === "user" ? "YOU" : "AI";

    const content = document.createElement("div");
    content.className = "message-content";

    const author = document.createElement("span");
    author.className = "message-author";
    author.textContent = role === "user" ? "You" : "Assistant";

    const bubble = document.createElement("div");
    bubble.className = "message-bubble";
    bubble.textContent = text;

    content.append(author, bubble);
    row.append(avatar, content);
    messages.append(row);
    messages.scrollTop = messages.scrollHeight;

    return row;
}

function addTypingIndicator() {
    const row = addMessage("assistant", "Thinking...");
    row.classList.add("typing");
    return row;
}

function setLoading(isLoading) {
    sendButton.disabled = isLoading;
    newChatButton.disabled = isLoading;
    messageInput.disabled = isLoading;
    sendButton.querySelector("span").textContent = isLoading ? "Sending..." : "Send";
}

chatForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = messageInput.value.trim();
    if (!message || sendButton.disabled) {
        return;
    }

    statusMessage.textContent = "";
    addMessage("user", message);
    messageInput.value = "";
    messageInput.style.height = "auto";

    setLoading(true);
    const typingIndicator = addTypingIndicator();

    try {
        const response = await fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ message })
        });

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(result.error || "The chat request failed.");
        }

        typingIndicator.remove();
        addMessage("assistant", result.reply || "The AI returned an empty response.");
    } catch (error) {
        typingIndicator.remove();
        addMessage("assistant", error.message || "Something went wrong. Please try again.");
        statusMessage.textContent = "Check your connection and the backend terminal for details.";
    } finally {
        setLoading(false);
        messageInput.focus();
    }
});

newChatButton.addEventListener("click", async () => {
    statusMessage.textContent = "";
    newChatButton.disabled = true;

    try {
        const response = await fetch("/api/chat", { method: "DELETE" });
        if (!response.ok) {
            throw new Error("Could not clear this conversation.");
        }

        messages.replaceChildren();
        addMessage("assistant", "New conversation started. What would you like to talk about?");
    } catch (error) {
        statusMessage.textContent = error.message || "Could not start a new conversation.";
    } finally {
        newChatButton.disabled = false;
        messageInput.focus();
    }
});

messageInput.addEventListener("input", () => {
    messageInput.style.height = "auto";
    messageInput.style.height = `${Math.min(messageInput.scrollHeight, 150)}px`;
});

messageInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        chatForm.requestSubmit();
    }
});
