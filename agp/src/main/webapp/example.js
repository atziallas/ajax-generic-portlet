//the ajaxUrl variable was drawn by the jsp file on top
// this hidden input was also drawn by the jsp
const csrfToken = document.getElementsByName('csrftoken')[0].value

const headers = new Headers(
    {
        'Content-Type': 'application/json',
        'csrf-token': document.getElementsByName('csrftoken')[0].value,
    }
)

const json = () => callApi("test")
const redirect = () => callApi("redirect")
const file = () => callApi("file")

const callApi = (arg) => fetch(ajaxUrl, {
    method: "POST",
    body: JSON.stringify({
        arg,
    }),
    headers
}).then(response => {
    return response.json();
}).then(response => {
    checkRedirect(response)
    console.log("Server ajax response:")
    console.log(response)
})

const checkRedirect = (response) => {
    if ("REDIRECT_URL" in response) {
        window.location.href = response["REDIRECT_URL"]
    }
}
