import axios from 'axios';
import React, { useEffect, useState } from 'react';

const CreateOrder = () => {
  const [customerOptions, setCustomerOptions] = useState([]);
  const [productOptions, setProductOptions] = useState([]);
  const [formData, setFormData] = useState({
    customer_id: '',
    product_id: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/customers');
        if (Array.isArray(response.data)) {
          setCustomerOptions(response.data);
        } else {
          setError('Error fetching customers: data is not an array');
        }
      } catch (error) {
        setError('Error fetching customers');
      }
    };

    const fetchProducts = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/products');
        if (Array.isArray(response.data)) {
          setProductOptions(response.data);
        } else {
          setError('Error fetching products: data is not an array');
        }
      } catch (error) {
        setError('Error fetching products');
      }
    };

    fetchCustomers();
    fetchProducts();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const orderData = {
        customer: { customer_id: formData.customer_id },
        product: { product_id: formData.product_id },
        date: new Date().toISOString()
      };
      const response = await axios.post('http://localhost:8080/api/v1/orders', orderData);
      console.log('Order created:', response.data);
      setSuccess('Order created successfully!');
      setError('');
      setFormData({
        customer_id: '',
        product_id: ''
      });
    } catch (error) {
      console.error('Error creating order:', error);
      setError('Error creating order');
      setSuccess('');
    }
  };

  return (
    <div>
      <h2>Create New Order</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {success && <p style={{ color: 'green' }}>{success}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          Customer:
          <select name="customer_id" value={formData.customer_id} onChange={handleInputChange}>
            <option value="">Select customer...</option>
            {customerOptions.map(customer => (
              <option key={customer.customer_id} value={customer.customer_id}>{customer.customer_name}</option>
            ))}
          </select>
        </label>
        <br />
        <label>
          Product:
          <select name="product_id" value={formData.product_id} onChange={handleInputChange}>
            <option value="">Select product...</option>
            {productOptions.map(product => (
              <option key={product.product_id} value={product.product_id}>{product.product_name}</option>
            ))}
          </select>
        </label>
        <br />
        <button type="submit">Create Order</button>
      </form>
    </div>
  );
};

export default CreateOrder;