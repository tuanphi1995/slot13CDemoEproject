import axios from 'axios';
import React, { useEffect, useState } from 'react';

const ShowAllOrders = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/orders/all');
        setOrders(response.data);
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    };

    fetchOrders();
  }, []);

  return (
    <div>
      <h2>Orders List</h2>
      <ul>
        {orders.map(order => (
          <li key={order.orderId}>
            <p>Order ID: {order.orderId}</p>
            <p>Order Date: {new Date(order.date).toLocaleDateString()}</p>
            <p>Customer: {order.customerName}</p>
            <p>Product: {order.productName}</p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ShowAllOrders;