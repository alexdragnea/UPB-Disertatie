import React, { useEffect, useContext } from 'react';
import axios from 'axios';
import AuthContext from '../AuthContext';

const FetchSensors = ({ setSensors }) => {
    const { user, refreshToken } = useContext(AuthContext);

    useEffect(() => {
        const fetchSensors = async () => {
            try {
                let token = sessionStorage.getItem('accessToken');
                if (!token) {
                    token = await refreshToken(); // Try refreshing the token if it's not present
                }

                const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}/v1/iot-core/measurements`, {
                    params: { userId: user.userId },
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                setSensors(response.data.measurements || []);
            } catch (err) {
                console.error('Error fetching sensors:', err);
                setSensors([]); // Set sensors as an empty array on error
            }
        };

        if (user) {
            fetchSensors();
        }
    }, [user, refreshToken, setSensors]);

    return null; // FetchSensors does not render anything
};

export default FetchSensors;
