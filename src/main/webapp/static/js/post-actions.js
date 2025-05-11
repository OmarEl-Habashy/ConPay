document.addEventListener('DOMContentLoaded', function() {
    const likeForm = document.getElementById('likeForm');
    if (likeForm) {
        likeForm.addEventListener('submit', function(e) {
            e.preventDefault();

            fetch(likeForm.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(new FormData(likeForm))
            })
                .then(response => response.json())  // Parse JSON response
                .then(data => {
                    // Update like button and count based on server response
                    const likeButton = likeForm.querySelector('.like-button');
                    const likeCount = likeForm.querySelector('.like-count');

                    // Update UI based on the server response
                    if (data.liked) {
                        likeButton.classList.add('liked');
                    } else {
                        likeButton.classList.remove('liked');
                    }

                    // Update count with the exact value from server
                    likeCount.textContent = data.count;
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    }
});