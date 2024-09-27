export const tokenIsExpired = (token) => {
    if (!token) return true;
    const payload = JSON.parse(atob(token.split('.')[1])); // Decode JWT
    return payload.exp * 1000 < Date.now(); // Check if token is expired
};
